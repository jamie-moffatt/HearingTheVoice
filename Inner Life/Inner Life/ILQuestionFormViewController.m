//
//  ILQuestionFormViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 20/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILQuestionFormViewController.h"

@interface ILQuestionFormViewController ()

@end

@implementation ILQuestionFormViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self)
    {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"Questions";
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)nextButton: (UIButton *)sender
{
    NSLog(@"Next Button");
}

- (void)previousButton: (UIButton *)sender
{
    NSLog(@"Previous Button");
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 1)
    {
        // Number of answers to current question
        return 5;
    }
    else
    {
        return 1;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0)
    {
        // TODO: work out question height
        return 60;
    }
    if (indexPath.section == 1)
    {
        // TODO: work out answer choice heights
        return MAX(([[NSString stringWithFormat:@"Choice %d", indexPath.row] sizeWithFont:[UIFont systemFontOfSize:14] constrainedToSize:CGSizeMake(280, 9000) lineBreakMode:NSLineBreakByWordWrapping].height) + 14, 44);
    }
    if (indexPath.section == 2)
    {
        return 50;
    }
    else return 44;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *TextCellIdentifier = @"TextCell";
    static NSString *ButtonCellIdentifier = @"ButtonCell";
    
    UITableViewCell *cell;
    
    if (indexPath.section == 2)
    {
        cell = [tableView dequeueReusableCellWithIdentifier:ButtonCellIdentifier];
    }
    else
    {
        cell = [tableView dequeueReusableCellWithIdentifier:TextCellIdentifier];
        
    }

    
    if (cell == nil)
    {
        if (indexPath.section == 2)
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ButtonCellIdentifier];
        }
        else
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:TextCellIdentifier];
        }
    }
    
    if (indexPath.section == 0)
    {
        cell.textLabel.font = [UIFont boldSystemFontOfSize:18];
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        cell.textLabel.text = [NSString stringWithFormat:@"Question"];
        
    }
    else if (indexPath.section == 1)
    {
        cell.textLabel.font = [UIFont systemFontOfSize:15];
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        cell.textLabel.text = [NSString stringWithFormat:@"Choice %d", indexPath.row];
        
    }
    else if (indexPath.section == 2)
    {
        UIButton* prevButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [prevButton addTarget:self action:@selector(previousButton:) forControlEvents:UIControlEventTouchDown];
        [prevButton setTitle:@"Previous" forState:UIControlStateNormal];
        prevButton.frame = CGRectMake(13, 4, cell.frame.size.width/2.0 - 15, 44.0);
        [cell addSubview:prevButton];
        
        UIButton* nextButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [nextButton addTarget:self action:@selector(nextButton:) forControlEvents:UIControlEventTouchDown];
        [nextButton setTitle:@"Next" forState:UIControlStateNormal];
        nextButton.frame = CGRectMake(cell.frame.size.width/2.0 + 2, 4, cell.frame.size.width/2.0 - 14, 44.0);
        [cell addSubview:nextButton];
    }
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
}

@end
