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

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        data = [ILDataSingleton instance];
        data.currentQuestion = 0;
        data.currentSection  = 9;
        
        data.prevResponseIDs = [[NSMutableDictionary alloc] init];
        data.prevResponseStrings = [[NSMutableDictionary alloc] init];
        data.prevResponseValues = [[NSMutableDictionary alloc] init];
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
    if (data.currentQuestion == ([[data getQuestionsBySection:data.currentSection] count] -1) && data.currentSection == 3)
    {
        NSLog(@"FINISH");
    }
    
    if (data.currentQuestion == ([[data getQuestionsBySection:data.currentSection] count] -1))
    {
        data.currentSection++;
        data.currentQuestion = 0;
    }
    else
    {
        data.currentQuestion++;
    }
    
    [self.tableView reloadData];
}

- (void)previousButton: (UIButton *)sender
{
    if (data.currentSection == 0 && data.currentQuestion == 0)
    {
        return;
    }
    
    data.currentQuestion--;
    if (data.currentQuestion < 0)
    {
        data.currentSection = (data.currentSection > 0) ? (data.currentSection - 1) : 0;
        data.currentQuestion = [[data getQuestionsBySection:data.currentSection] count] -1;
        
        if (data.currentSection < 0)
        {
            data.currentSection = 0;
        }
    }
    [self.tableView reloadData];
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
        NSArray *choices = [self getCurrentSection].choices ;
        return [choices count];
    }
    else
    {
        return 1;
    }
}

- (ILSection *)getCurrentSection
{
    ILSection *section = [[[ILDataSingleton instance] getQuestionsInSections] objectAtIndex:[ILDataSingleton instance].currentSection];
    return section;
}

- (ILQuestion *)getCurrentQuestion:(ILSection *)section
{
    ILQuestion *question = [section.questions objectAtIndex:[ILDataSingleton instance].currentQuestion];
    return question;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ILSection *section = [self getCurrentSection];
    ILQuestion *question = [self getCurrentQuestion:section];
    
    if (indexPath.section == 0)
    {
         return MAX(([question.questionDescription sizeWithFont:[UIFont italicSystemFontOfSize:15] constrainedToSize:CGSizeMake(280, 9000) lineBreakMode:NSLineBreakByWordWrapping].height) + 14, 44);
    }
    if (indexPath.section == 1)
    {
        ILChoice *choice = [section.choices objectAtIndex:indexPath.row];
        return MAX(([choice.text  sizeWithFont:[UIFont systemFontOfSize:15] constrainedToSize:CGSizeMake(280, 9000) lineBreakMode:NSLineBreakByWordWrapping].height) + 14, 44);
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
        cell.textLabel.font = [UIFont italicSystemFontOfSize:15];
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        cell.textLabel.text = [self getCurrentQuestion:[self getCurrentSection]].questionDescription;
        
    }
    else if (indexPath.section == 1)
    {
        ILChoice *choice = [[self getCurrentSection].choices objectAtIndex:indexPath.row];
        cell.textLabel.font = [UIFont systemFontOfSize:15];
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        cell.textLabel.text = choice.text;
        
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
