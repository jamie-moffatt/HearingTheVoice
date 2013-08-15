//
//  ILEndViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 15/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ILEndViewController : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *lblSamplesComplete;
@property (weak, nonatomic) IBOutlet UILabel *lblAverageResponseTime;

- (IBAction)yesButton:(UIButton *)sender;
- (IBAction)noButton:(UIButton *)sender;

@end
