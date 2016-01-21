//
//  ILSettingsViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 07/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILSettingsViewController.h"
#import "common.h"
#import "TestFlight.h"

@interface ILSettingsViewController ()

@end

@implementation ILSettingsViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
    
    }
    return self;
}

-(void)loadView
{
    UIView *view = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
    [view setBackgroundColor:[UIColor whiteColor]];
    
    CGFloat width = view.frame.size.width;
    CGFloat height = view.frame.size.height - NAVIGATION_BAR_HEIGHT;
    
    amTime = [ILAppManager getAMNotificationTime];
    pmTime = [ILAppManager getPMNotificationTime];
    
    DURoundedBorderLabel *lblPageDescription = [[DURoundedBorderLabel alloc] initWithFrame:CGRectMake(10, 10, width - 20, 0)];
    lblPageDescription.font = [UIFont systemFontOfSize:13];
    lblPageDescription.text = @"Slide the segments to change notification times. Changes will only be applied the next day.";
    lblPageDescription.textAlignment = NSTextAlignmentLeft;
    lblPageDescription.backgroundColor = [UIColor colorWithWhite:0.92 alpha:1];
    [lblPageDescription sizeToFitFixedWidth:width - 20];
    
    CGRect f = lblPageDescription.frame;
    
    lblTimeRangeDescription = [[DURoundedBorderLabel alloc] initWithFrame:CGRectMake(10, f.origin.y + f.size.height + 10, width - 20, 0)];
    lblTimeRangeDescription.font = [UIFont systemFontOfSize:13];
    lblTimeRangeDescription.text =
    [NSString stringWithFormat:
     @"You will be notified between %ld:00 - %ld:00 and %ld:00 - %ld:00.",
     (long)amTime, (amTime + 3) % 24,
     (long)pmTime, (pmTime + 3) % 24];
    lblTimeRangeDescription.textAlignment = NSTextAlignmentLeft;
    lblTimeRangeDescription.backgroundColor = [UIColor colorWithWhite:0.92 alpha:1];
    [lblTimeRangeDescription sizeToFitFixedWidth:width - 20];
    
    f = lblTimeRangeDescription.frame;
    
    timeSelector = [[ILTimeSelector alloc] initWithFrame:CGRectMake(0, f.origin.y + f.size.height + 5, width, height - (f.origin.y + f.size.height + 10))];
    timeSelector.backgroundColor = [UIColor whiteColor];
    
    timeSelector.amStartTime = amTime;
    timeSelector.pmStartTime = pmTime;
    timeSelector.delegate = self;
    
    [view addSubview:lblPageDescription];
    [view addSubview:lblTimeRangeDescription];
    [view addSubview:timeSelector];
    
    self.view = view;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    [TestFlight passCheckpoint:@"Opened Settings"];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)timeDidChange:(NSInteger)amStartTime :(NSInteger)pmStartTime
{
    amTime = amStartTime;
    pmTime = pmStartTime;
    
    [ILAppManager setAMNotificationTime:amStartTime];
    [ILAppManager setPMNotificationTime:pmStartTime];
    
    [ILAppManager changeNotifications:amStartTime :pmStartTime];
    
    lblTimeRangeDescription.text =
    [NSString stringWithFormat:
     @"You will be notified between %ld:00 - %ld:00 and %ld:00 - %ld:00.",
     (long)amTime, (long)((amTime + 3) % 24),
     (long)pmTime, (long)((pmTime + 3) % 24)];
}

@end
